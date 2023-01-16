package once;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * 导入星球用户到数据库
 */
public class importXingQiuUser
{
    public static void main(String[] args) {
        String fileName = "D:\\workspace_idea\\user_center\\src\\main\\resources\\星球数据.xlsx";
        List<XingQiuTableUserInfo> list = EasyExcel.read(fileName).head(XingQiuTableUserInfo.class).sheet().doReadSync();
        for (XingQiuTableUserInfo data : list) {
            System.out.println(data);
        }
    }

}
