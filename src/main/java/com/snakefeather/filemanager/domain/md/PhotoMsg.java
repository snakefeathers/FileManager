package com.snakefeather.filemanager.domain.md;

import com.snakefeather.filemanager.domain.TextDiv;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * markdown 中，从以下格式信息中，提取出需要的信息。
 * ![img](photos/535.jpg)
 * ![image-20220313173812070](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20220313173812070.png)
 * ![img](https://lab.huaweicloud.com/img/tc/sp-im/1519/1567681665-step-1.png)
 * ![运行结果](/photos/image-20211221213605502.png)
 * <img src="photos/依赖倒转原则.png" style="zoom:80%;" />
 * <img src="photos\依赖倒转原则.png" style="zoom:80%;" />
 * <img src="https://lab.huaweicloud.com/img/tc/sp-im/1518/1584001714-step-0.png" style="zoom:80%;" />
 * <img src="E:\notes\main\notes\photos\image-20220319181759862.png" style="zoom:80%;" />
 *
 *   注：
 *      * 此接口的实例化暂且是可以强转为 TextDiv 类型的。
 *      * 计划修改此接口为抽象接口，不过暂且不必。
 *      * TextDiv 曾计划转为接口。  不过碍于装饰器基类的身份，是不可行的。
 *      * （挖坑） 感觉这种强转 很不舒服，就怕哪天出bug。   不过短时间无忧，且个人水平不足以优化，暂且跳过。
 */
public interface PhotoMsg {


    //#region  getter() setter()  图片名  图片路径  图片路径类型   注释

    //  修改、获取   图片名  // 注：是关联修改，修改链接的同时，会修改对应的图片
    String getPhotoName();
    void setPhotoName(String fileName);

    // 获取、修改 图片路径   // 所有文件的图片路径为统一种类。（绝对、相对、网络中的一种。） // 修改时不用同时考虑三种情况下的修改。
    String getPhotoPath();
    void setPhotoPath(String folderPath);

    //  获取、修改 图片路径类型
    PathType getPathType();
    void setPathType(PathType pathType);

    //  获取、修改 注释
    String getRemark();
    void setRemark(String remark);

    //#endregion

    //  装饰
    void decorate(TextDiv textDiv);

    enum PathType {
        WEB, ABSOLUTE, RELATIVE
    }

}
