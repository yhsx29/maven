package com.slf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

import javax.swing.JOptionPane;

public class MavenClean {
	// 仓库根目录
	private static final String PATH = "E:\\mavenRepository\\Repository";
    private static long total;
    private static long time;

    public static void main(String[] args) {
    	JOptionPane jop = new JOptionPane();
    	String mavenPath = null;
    	URL url = MavenClean.class.getProtectionDomain().getCodeSource().getLocation();
    	String path = url.getPath();
    	path = path.substring(1,path.lastIndexOf('/'));
    	File config = new File(path.substring(1,path.lastIndexOf("/"))+"/config.property");
    	if(config.exists() && config.isFile()) {
    		try {
				Properties prop = new Properties();
				prop.load(new FileInputStream(config));
				mavenPath = prop.getProperty("REPOSITORY_HOME");
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	if(mavenPath == null)
    		mavenPath =PATH;
        File dir = new File(mavenPath);
        System.out.println("mavenPath:" + mavenPath);
        if (!dir.isDirectory()) {
            System.out.println("请输入Maven仓库所在路径");
            System.exit(0);
        } else {
            Instant startNow = Instant.now();
            MavenClean.delFile(dir);
            Instant endNow = Instant.now();
            time = Duration.between(startNow, endNow).toMillis();
        }
        System.out.printf("删除数量：%d 耗时：%d 毫秒", total, time);
    }

    public static void delFile(File file) {
        File[] list = file.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                delFile(f);
                if (f.getName().equals("unknown")) {
                    delAll(f);
                    total++;
                    System.out.println("删除：" + f.getAbsolutePath());
                } else if (f.getName().startsWith("${") && f.getName().endsWith("}")) {
                    // 如果 文件夹名称是以 ${ 开头 } 结尾，那么将这个文件夹及其下面所有文件全部删除
                    delAll(f);
                    f.delete();
                    total++;
                    System.out.println("删除：" + f.getAbsolutePath());
                } else if (f.listFiles().length == 0) {
                    // 删除空文件夹
                    f.delete();
                    total++;
                    System.out.println("删除：" + f.getAbsolutePath());
                }
            } else {
                if (f.getName().endsWith(".lastUpdated")) {
                    f.delete();
                    total++;
                    System.out.println("删除：" + f.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 删除文件夹下的所有文件夹、文件及其子文件夹、文件
     *
     * @param file file
     */
    public static void delAll(File file) {
        File[] list = file.listFiles();
        for (File f : list) {
            if (f.isFile()) {
                // 是文件就删除
                f.delete();
                total++;
                System.out.println("删除：" + f.getAbsolutePath());
            } else {
                // 先将文件夹下的文件夹和文件全部删除再删除源文件夹
                delAll(f);
                f.delete();
                total++;
                System.out.println("删除：" + f.getAbsolutePath());
            }
        }
    }

}
