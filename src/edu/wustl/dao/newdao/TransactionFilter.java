
package edu.wustl.dao.newdao;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import edu.wustl.common.util.logger.Logger;

public class TransactionFilter implements Filter
{

	private static final Logger logger = Logger.getCommonLogger(TransactionFilter.class);
	private String userTransactionJndiName;
	
	public void destroy()
	{
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		UserTransaction transaction = null;

		try
		{
			transaction = (UserTransaction) new InitialContext()
					.lookup(userTransactionJndiName);
			transaction.begin();
			chain.doFilter(request, response);
			if(!isToRollback(request))
			{
				logger.info("Commiting Transaction");
				transaction.commit();
			}
			else
			{
				logger.error("Transaction is getting rollback due to no actionstatus found.");
				transaction.rollback();
			}
		}
		catch (final Throwable errorInServlet)
		{
			try
			{
				if(transaction!=null)
					transaction.rollback();
			}
			catch (final Exception rollbackFailed)
			{
				logger.error("Transaction failed !", rollbackFailed);
			}
			//throw new ServletException(errorInServlet);
		}
	}

	private boolean isToRollback(ServletRequest request)
	{
		boolean isToRollBack = false;
		ActionStatus actionStatus = (ActionStatus)request.getAttribute(ActionStatus.ACTIONSTAUS);
		if(actionStatus!=null&&actionStatus.isFailureAction())
		{
			isToRollBack = true;
		}
		return isToRollBack;
	}
	public void init(FilterConfig filterConfig) throws ServletException
	{
		userTransactionJndiName = filterConfig.getInitParameter("userTransactionJndiName");

	}

}
