package experiments;

import org.apache.log4j.Logger;

/**
 * @author Jorge
 * @date 3/Mar/2010
 * @time 16:34:54
 * @mail machadofisher@gmail.com
 * @copyright ModiPlace 2010
 * @site www.modiplace.pt
 */
public class TextLog4j
{
    private static final Logger logger = Logger.getLogger(TextLog4j.class);
    public static void main(String[] args) throws InterruptedException {

        while(true)
        {
            Thread.sleep(1000);
            logger.info("teste");
        }


    }
}
