
package edu.wustl.dao.newdao;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
			transaction.commit();
		}
		catch (final Exception errorInServlet)
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
			throw new ServletException(errorInServlet);
		}
	}

	private ActionStatus actionStatus(ServletRequest request) 
	{
        ActionStatus actionStatus = (ActionStatus) request.getAttribute("actionStatus");
        if (actionStatus == null) 
        {
            actionStatus = ActionStatus.SUCCESSFUL;
        } 
        return actionStatus;
    }
	
	public void init(FilterConfig filterConfig) throws ServletException
	{
		userTransactionJndiName = filterConfig.getInitParameter("userTransactionJndiName");

	}

}
