package com;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;

import javax.swing.*;
import java.awt.*;

public class ZookeeperConnectTest extends JFrame {
    private JTextArea textArea = new JTextArea();

    private void init() throws Exception {
        textArea.setFont(new Font("StSong", Font.BOLD, 16));
        this.setLayout(new GridLayout());
        this.add(new JScrollPane(textArea));
        /*
         *  RetryPolicy： 失败的重试策略的公共接口
         *  ExponentialBackoffRetry是 公共接口的其中一个实现类
         *      参数1： 初始化sleep的时间，用于计算之后的每次重试的sleep时间
         *      参数2：最大重试次数
         参数3（可以省略）：最大sleep时间，如果上述的当前sleep计算出来比这个大，那么sleep用这个时间
         */
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000, 1);

        String result = JOptionPane.showInputDialog(null, "请输入ip和端口:",
                "127.0.0.1:2181");
        String node = JOptionPane.showInputDialog(null, "请输入监听节点:",
                "/");
        this.setTitle(result+ "的" + node + "节点监听中");
        //创建客户端
        /*
         * 参数1：连接的ip地址和端口号
         * 参数2：会话超时时间，单位毫秒
         * 参数3：连接超时时间，单位毫秒
         * 参数4：失败重试策略
         */
        CuratorFramework client = CuratorFrameworkFactory.newClient(result, 3000, 3000, retry);

        //监听拦截并指定节点
        TreeCache cache = new TreeCache(client, "/");

        //启动服务
        client.start();

        //String path = client.create().forPath("/c");
        //String s = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/d", "hello".getBytes());
        //NodeCache nodeCache = new NodeCache(client, "/");
        //参数为true：可以直接获取监听的节点
        //nodeCache.start(true);

        cache.start();

        cache.getListenable().addListener((curator, event) -> {
            if(event.getType() == TreeCacheEvent.Type.NODE_ADDED){
                String s = "添加子节点 " + "节点:" + event.getData().getPath() + " 数据:" + new String(event.getData().getData()) + "\n";
                textArea.insert(s, textArea.getCaretPosition());
            }else if (event.getType() == TreeCacheEvent.Type.NODE_REMOVED){
                String s = "移除子节点 " + "节点:" + event.getData().getPath() + " 数据:" + new String(event.getData().getData()) + "\n";
                textArea.insert(s, textArea.getCaretPosition());
            }else if(event.getType() == TreeCacheEvent.Type.NODE_UPDATED){
                String s = "修改子节点 " + "节点:" + event.getData().getPath() + " 数据:" + new String(event.getData().getData()) + "\n";
                textArea.insert(s, textArea.getCaretPosition());
            }else if(event.getType() == TreeCacheEvent.Type.INITIALIZED){
                textArea.insert("初始化完成\n", textArea.getCaretPosition());
                System.out.println("初始化完成");
            }else if(event.getType() ==TreeCacheEvent.Type.CONNECTION_SUSPENDED){
                textArea.insert("连接过时\n", textArea.getCaretPosition());
            }else if(event.getType() ==TreeCacheEvent.Type.CONNECTION_RECONNECTED){
                textArea.insert("重新连接\n", textArea.getCaretPosition());
            }else if(event.getType() ==TreeCacheEvent.Type.CONNECTION_LOST){
                textArea.insert("连接失效后稍等一会儿执行\n", textArea.getCaretPosition());
            }

            /*if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                String s = "子节点更新 " + "节点:" + event.getData().getPath() + " 数据:" + new String(event.getData().getData()) + "\n";
                textArea.insert(s, textArea.getCaretPosition());
            } else if (event.getType() == PathChildrenCacheEvent.Type.INITIALIZED) {
                textArea.insert("初始化操作\n", textArea.getCaretPosition());
            } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                String s = "删除子节点 " + "节点:" + event.getData().getPath() + " 数据:" + new String(event.getData().getData()) + "\n";
                textArea.insert(s, textArea.getCaretPosition());
            } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                String s = "添加子节点 " + "节点:" + event.getData().getPath() + " 数据:" + new String(event.getData().getData()) + "\n";
                textArea.insert(s, textArea.getCaretPosition());
            } else if (event.getType() == PathChildrenCacheEvent.Type.CONNECTION_SUSPENDED) {
                textArea.insert("连接失效\n", textArea.getCaretPosition());
            } else if (event.getType() == PathChildrenCacheEvent.Type.CONNECTION_RECONNECTED) {
                textArea.insert("重新连接\n", textArea.getCaretPosition());
            } else if (event.getType() == PathChildrenCacheEvent.Type.CONNECTION_LOST) {
                textArea.insert("连接失效后稍等一会儿执行\n", textArea.getCaretPosition());
            }*/
        });

        //textArea.append(nodeCache.getCurrentData().toString() + "\n");

       /* nodeCache.getListenable().addListener(() -> {
            textArea.append(nodeCache.getCurrentData().getPath() + "\n");
            textArea.append(new String(nodeCache.getCurrentData().getData()) + "\n");
        });*/

        this.setBounds(500, 400, 400, 600);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);

        //关闭服务
        //client.close();
        //System.in.read();
    }

    public static void main(String[] args) {
        try {
            new ZookeeperConnectTest().init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


