package luluouter.msg.inner;

public abstract class InnerClient {
    // private final static byte FLAG_MSG = 73;
    // private final static byte FLAG_DATA = 66;
    //
    // public static InnerClient createClient(Socket inner) {
    // CompletableFuture<InnerClient> executor =
    // CompletableFuture.supplyAsync(() -> {
    // InnerClient innerClient = null;
    // try {
    // InputStream inputStream = inner.getInputStream();
    // int flagInt = inputStream.read();
    // byte flag = (byte) (flagInt & 0xff);
    // switch (flag) {
    // case FLAG_MSG:
    // innerClient = new InnerMsgClient(inner);
    // break;
    // case FLAG_DATA:
    // innerClient = new InnerDataClient(inner);
    // break;
    // }
    //
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return innerClient;
    // });
    // try {
    // return executor.get();
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // } catch (ExecutionException e) {
    // e.printStackTrace();
    // }
    // return null;
    // }
    //
    // public abstract void init();
}
