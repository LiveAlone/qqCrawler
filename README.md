QingQing 试卷模板内容的抓取方式
1. 配置参数
    xp.password 用户登陆以后， 获取页面的xp.password 参数信息
    city - year - charge - cateid cateid 为空 默认的不限试卷类型
    regular 页面抓取正则匹配的方式, 获取对应的Download 页面内容
```
    year 范围: 
        -1 不限
        2005 - 2017 整数范围
    city 范围：
        -1 不限
        provinceid=1 全国
        provinceid=4 上海
        provinceid=2 北京
        provinceid=3 天津
        provinceid=5 重庆
        provinceid=10 山东
        provinceid=14 江苏
        provinceid=13 浙江
        provinceid=16 广东
        provinceid=21 湖南
        provinceid=19 河南
        provinceid=6 河北
        provinceid=20 湖北
        provinceid=15 江西
        provinceid=7 辽宁
        provinceid=8 黑龙江
        provinceid=9 吉林
        provinceid=11 山西
        provinceid=12 安徽
        provinceid=17 福建
        provinceid=18 海南
        provinceid=22 四川
        provinceid=23 云南
        provinceid=24 贵州
        provinceid=25 陕西
        provinceid=26 甘肃
        provinceid=27 青海
        provinceid=28 宁夏
        provinceid=29 内蒙古
        provinceid=30 广西
        provinceid=31 西藏
        provinceid=32 新疆
        provinceid=33 香港
        provinceid=34 澳门
        provinceid=35 台湾
    charge : 1 收费 0 免费
    cateid : 不传默认不去区分试卷的类型
        cateid=-1 所有的试卷类型（不限）
        cateid=101开学考试
        cateid=102月考
        cateid=103期中
        cateid=104期末
        cateid=105学业
        cateid=106联考
        cateid=107调研
        cateid=108单元测试
        cateid=109竞赛
        cateid=110模拟预测
        cateid=111真题
        cateid=112专题汇编
        cateid=113同步测试
        cateid=117自主招生
    demo:
        4,2017,0,101 (上海， 2017年， 免费， 开学考试试卷)
        
```

2. load 文件配置方式
    LoadFileUrl.txt 解析Paged 页面后 Loader 的Url
    LoadLog.txt 对应的解析已经Load的页面信息, 重复执行获取其中的ERROR 执行方式.
    
    