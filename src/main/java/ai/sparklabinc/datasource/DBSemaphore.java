package ai.sparklabinc.datasource;

/**
 * 数据库同步对象
 *
 * @author Damon
 */
public class DBSemaphore {
    private static volatile boolean m_bInUse = false;

    public DBSemaphore() {
    }

    /**
     * 设置"使用标志"。 传入true表示请求“使用标志”，传入false表示释放“使用标志”。
     *
     * @param bNewValue boolean
     * @return boolean
     */
    protected static synchronized boolean setInUseFlag(boolean bNewValue) {
        if (bNewValue == true) {
            // 请求“使用标志”
            if (m_bInUse == true) {
                // “使用标志”已经被占用
                return false;
            } else {
                m_bInUse = true;
                return true;
            }
        } else {
            // 释放“使用标志”
            m_bInUse = false;
            return true;
        }
    }

    protected static void lock() throws Exception {
        lock(0);
    }

    protected static void lock(int nSeconds) throws Exception {
        if (nSeconds <= 0) {
            while (!setInUseFlag(true)) {
                Thread.sleep(100);
            }
        } else {
            while (!setInUseFlag(true) && nSeconds-- > 0) {
                Thread.sleep(100);
            }

            if (nSeconds == 0) {
                throw new Exception("Lock time out");
            }
        }
    }

    protected static void unLock() {
        setInUseFlag(false);
    }
}