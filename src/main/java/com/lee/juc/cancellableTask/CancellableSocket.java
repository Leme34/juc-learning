package com.lee.juc.cancellableTask;

import lombok.Setter;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.net.Socket;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * 定制化的 Cancellable 案例
 *
 * 最常见的阻塞I/O就是对同步的 Socket I/O 进行读写，
 * 虽然 InputStream、OutputStream 的 read 或 write 等方法不会响应中断
 * 但是此案例可以通过关闭底层套接字，使得因执行读写操作而被阻塞的线程抛出一个 SocketException
 *
 * Created by lsd
 * 2019-10-25 01:02
 */
public abstract class CancellableSocket<T> implements Cancellable<T> {
    @Setter
    private Socket socket;

    @Override
    public synchronized void cancel() {
        if (socket != null) {
            IOUtils.closeQuietly(socket);
        }
    }

    /**
     * 定制取消操作的 FutureTask。
     * 不仅调用 CancellableSocket.this.cancel() 执行了可定制的任务取消行为：关闭底层 socket，使其达到响应中断的效果，
     * 还通过 super.cancel 调用了 Future接口的 cancel，使线程中断
     */
    @Override
    public RunnableFuture<T> newTask() {
        return new FutureTask<>(this) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    // 执行定制化的任务取消行为
                    CancellableSocket.this.cancel();
                } finally {
                    // 使线程中断
                    return super.cancel(mayInterruptIfRunning);
                }
            }
        };
    }
}
