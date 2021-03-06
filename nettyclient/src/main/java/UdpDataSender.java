import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

public class UdpDataSender {
    private String host;
    private int port;

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;

    UdpDataSender(String host,int port){
        try {
            this.host = host;
            this.port = port;
            group = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    .remoteAddress(host, port)
                    .handler(new UdpDataHandler());
            channel = bootstrap.bind(0).sync().channel();

        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void close(){
        group.shutdownGracefully();
    }

    public void send(byte[] data)throws Exception{

        try {
            ByteBuf byteBuf =  Unpooled.buffer(data.length);
            byteBuf.writeBytes(data);

            channel.writeAndFlush(
                    new DatagramPacket(
                            byteBuf,
                            new InetSocketAddress(
                                    host,port
                            )));
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public static void main(String[] args) throws Exception{
        String host="127.0.0.1";
        int port = 10001;

        UdpDataSender udpDataSender = new UdpDataSender(host,port);
        for (int i = 0; i < 100; i++) {
            String data="hello world "+i;
            udpDataSender.send(data.getBytes());
            Thread.sleep(2);
        }
        udpDataSender.close();
    }
}
