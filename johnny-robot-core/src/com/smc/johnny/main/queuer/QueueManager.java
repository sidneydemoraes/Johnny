package com.smc.johnny.main.queuer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.smc.johnny.main.application.model.Order;
import com.smc.johnny.main.application.model.Order.ErrorType;
import com.smc.johnny.main.application.repositories.OrderRepository;

/**
 * <p>This class is used to manage a queue of Objects. It's implementation must
 * be updated according to the application in which it will be used in order to 
 * adapt it to the desired object type.</p>
 * <p>In this particular case, it's using an {@link Order} object.</p>
 * @author smcoelho
 */
public class QueueManager {

	private static List<Order> queue;
	private static Logger log = Logger.getRootLogger();

	/**
	 * Builds a queue, pulling Orders from a repository.
	 */
	public static void buildQueue(){
		clearQueue();
		log.info("Building queue");
		queue = OrderRepository.getPendingOrders();
	}

	/**
	 * Clears the queue.
	 */
	public static void clearQueue(){
		log.info("Clearing queue");
		queue = null;
		queue = new ArrayList<Order>();
	}
	
	/**
	 * Removes the first item from the queue.
	 */
	public static void removeCurrentItem(){
		log.info("Removing current item from queue");
		queue.remove(0);
	}

	/**
	 * Gets the first item from the queue.
	 * @return an {@link Order}
	 */
	public static Order getCurrentItem(){
		log.info("Getting current Item from queue");
		if(queue == null)
			buildQueue();
		
		if(queue.size()==0)
			return null;

		return queue.get(0);
	}

	/**
	 * Sets the current item as completed.
	 */
	public static void setCompleted(){
		log.info("Setting current item as completed");
		queue.get(0).setCompleted(true);
		queue.get(0).setCompletedDate(new DateTime().toString("MM/dd/yy"));
		try {
			OrderRepository.update(getCurrentItem());
		} catch (SQLException e) {
			log.error("Error while setting order completed", e);
			System.exit(0);
		}
		removeCurrentItem();
	}

	/**
	 * Sets the current item as with errors and marks item as completed so it is not 
	 * worked again.
	 * @param {@link ErrorType} error
	 */
	public static void setError(ErrorType error){
		log.info("Setting current item with error " + error.toString());
		queue.get(0).setError(true);
		queue.get(0).setErrorType(error);
		setCompleted();
	}

	/**
	 * Gets the size of the queue.
	 * @return int size
	 */
	public static int getSize(){
		return queue.size();
	}
}
