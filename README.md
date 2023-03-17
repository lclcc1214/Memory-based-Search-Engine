# 基于内存的搜索引擎
## 项目介绍
&emsp;基于内存的英文全文检索搜索引擎，可将指定目录下的一批.txt格式的文本文件扫描并在内存里建立倒排索引。基于构建好的索引，可实现的功能如下所示：
- 单个搜索关键词的全文检索
- 二个搜索关键词的全文检索
- 包含二个单词的短语检索，其中这二个单词必须在作为短语文档里出现，它们的位置必须是相邻

## 文件组织结构
&emsp;bin 目录存放项目代码编译后的.class文件 <br>
&emsp;index 目录存放建立好的倒排索引文件index.dat <br>
&emsp;javadoc 目录存放整个项目的JavaAPI帮助文档 <br>
&emsp;model 目录存放包含index、parse、query三个模块的UML图以及对应的png <br>
&emsp;src 目录存放项目代码 <br>
&emsp;text 目录存放搜索文本 <br>

## 使用说明

### 运行环境
    JDK版本：jdk-13.0.2 
    IDE：IntelliJ IDEA 2021.3.2 (Ultimate Edition)

### 具体使用
#### 方法一
- 用户可以使用命令行直接运行已经生成好的的class文件<br>
#### 方法二
- 在 `text` 目录下存放所有需要搜索的txt文档
- 使用包 `src.hust.cs.javacourse.search.run` 中的 TestBuildIndex 编译运行来构建索引，程序会将构建好的索引序列化到二进制文件
- 在包 `src\hust\cs\javacourse\search\run\TestSearchIndex` 的 main 函数中修改搜索方式，然后编译运行该文件，检索获得的命中文档的信息在控制台输出。