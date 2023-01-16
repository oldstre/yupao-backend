package once;

import com.alibaba.excel.EasyExcel;
import java.util.List;

/**
 * 导入 Excel
 * @author hzz
 * @create 2022-06-13 13:04
 */
public class ImportExcel {
    public static void main(String[] args) {
        //文件路径
        String fileName = "D:\\workspace_idea\\user_center\\src\\main\\resources\\星球数据.xlsx";
        // listenerRead(fileName);

        synchronousRead(fileName);
    }

    public static void listenerRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭user
        // 这里每次会读取100条数据 然后返回过来 直接调用使用数据就行
        EasyExcel.read(fileName, XingQiuTableUserInfo.class, new XingQiuTableUserInfoListener()).sheet().doRead();
    }

    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuTableUserInfo> list = EasyExcel.read(fileName).head(XingQiuTableUserInfo.class).sheet().doReadSync();
        for (XingQiuTableUserInfo data : list) {
            System.out.println(data);
        }
    }

}