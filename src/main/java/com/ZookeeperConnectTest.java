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
        //RetryPolicy： 失败的重试策略的公共接口
        //ExponentialBackoffRetry是 公共接口的其中一个实现类

        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000, 1);

        String result = JOptionPane.showInputDialog(null, "请输入ip和端口:",
                "127.0.0.1:2181");
        String node = JOptionPane.showInputDialog(null, "请输入监听节点:",
                "/");
        this.setTitle(result + "的" + node + "节点监听中");

        //创建客户端
        CuratorFramework client = CuratorFrameworkFactory.newClient(result, 3000, 3000, retry);

        //监听拦截并指定节点
        TreeCache cache = new TreeCache(client, node);

        //启动服务
        client.start();

        cache.start();

        cache.getListenable().addListener((curator, event) -> {
            if (event.getType() == TreeCacheEvent.Type.NODE_ADDED) {
                String s = "添加子节点 " + "节点:" + event.getData().getPath() + " 数据:" + new String(event.getData().getData()) + "\n";
                textArea.insert(s, textArea.getCaretPosition());
            } else if (event.getType() == TreeCacheEvent.Type.NODE_REMOVED) {
                String s = "移除子节点 " + "节点:" + event.getData().getPath() + " 数据:" + new String(event.getData().getData()) + "\n";
                textArea.insert(s, textArea.getCaretPosition());
            } else if (event.getType() == TreeCacheEvent.Type.NODE_UPDATED) {
                String s = "修改子节点 " + "节点:" + event.getData().getPath() + " 数据:" + new String(event.getData().getData()) + "\n";
                textArea.insert(s, textArea.getCaretPosition());
            } else if (event.getType() == TreeCacheEvent.Type.INITIALIZED) {
                textArea.insert("初始化完成\n", textArea.getCaretPosition());
                System.out.println("初始化完成");
            } else if (event.getType() == TreeCacheEvent.Type.CONNECTION_SUSPENDED) {
                textArea.insert("连接过时\n", textArea.getCaretPosition());
            } else if (event.getType() == TreeCacheEvent.Type.CONNECTION_RECONNECTED) {
                textArea.insert("重新连接\n", textArea.getCaretPosition());
            } else if (event.getType() == TreeCacheEvent.Type.CONNECTION_LOST) {
                textArea.insert("连接失效后稍等一会儿执行\n", textArea.getCaretPosition());
            }

        });

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


