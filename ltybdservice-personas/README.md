#本工程是蓝泰源大数据用户画像工程
##1、数据仓库


1、用户的历史gps坐标信息，存储于hive仓库(10.1.254.12:app.dwd_user_gps_tmp)

userId|longitude|latitude|phoneModel|currTime|cityCode|geocode6|geocode7|geocode8
:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:
string|string|string|string|string|string|string|string|string|
4036|113.938740|22.561402|860443038952215|2017-11-13 19:00:43|130400|ws102e|ws102ep|ws102ept

注意userId可能为空

##2、应用分析
1、OD分析

od是一个区域，该区域可以是一个站点，也可以是一个geohash编码所代表的区域
将数据仓库的原始数据整理成如下格式

userId|longitude|latitude|phoneModel|currentTime|cityCode|region
:---:|:---:|:---:|:---:|:---:|:---:|:---:
string|string|string|string|string|string|string|
4036|113.938740|22.561402|860443038952215|2017-11-13 19:00:43|130400|ws102e

其中region代表用户在记录时间的od区域
统计用户一周内在不同时间类型（分五种类型，分别是：工作日上午，工作日下午，周五下午，周末上午，周末下午），在不同区域的信息量，
取信息量最大的两个作为候选od点，滤除非od点的用户坐标信息，将候选信息再按天分组，在每一天（其实只有半天）内按时间先后排序，
最开始的区域是起始区域，即o点，检查区域位置是否发生改变，发生改变，则说明到达终点，即d点，可以得到一趟行程
在某种类型时间的半天内可能有多次反复，取时间最长的一次作为当天特定时间类型的行程，取某种时间类型的所有起始时间的平均值作为该类型的起始时间，
取该时间类型的所有终止时间平均作为该类型的终止时间。得到每个用户的行程，剔除明显不对的数据，然后判断该用户是否需要换乘。是否换乘暂时无效

输出结果表如下（地址：jdbc:mysql://10.1.254.12:3306:personas.personas）：

userId|phoneModel|cityCode|timeType|originRegion|originPointCount|originDateCount|startDayTime|destRegion|destPointCount|destDateCount|endDayTime|transfer
:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:
text|text|text|text|text|text|text|text|text|text|text|text|bit(1)

其中：originPointCount代表用于分析的起始点数据量，originDateCount代表用于分析起始点的数据天数，destPointCount与destDateCount的含义与origin所描述相似。

注意：换乘信息暂时全部为写死

hive仓库地址通过resources目录下的三个xml文件进行配置。




