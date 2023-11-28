package com.whale.lack.once;

import com.alibaba.excel.EasyExcel;

import java.util.List;

public class ImportExcel {
    /**
     * 最简单的读
     * <p>
     * 1. 创建excel对应的实体对象
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器
     * <p>
     * 3. 直接读即可
     */
    public static void main(String[] args) {


        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = " ";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        readByLister(fileName);
        synchronousRead(fileName);

    }

    /**
     * 监听器读取
     * @param fileName
     */
   public static void readByLister(String fileName){
       EasyExcel.read(fileName, IndexOrNameData.class, new TableListener()).sheet().doRead();

   }

    /**
     * 同步读取
     * @param fileName
     */
    public static void synchronousRead(String fileName){
        List<IndexOrNameData> list =
                EasyExcel.read(fileName).head(IndexOrNameData.class).sheet().doReadSync();
        for (IndexOrNameData data : list) {
            System.out.println(data);
    }
}
}