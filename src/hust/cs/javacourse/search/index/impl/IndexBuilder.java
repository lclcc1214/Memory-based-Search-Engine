package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class IndexBuilder extends AbstractIndexBuilder {
    public IndexBuilder(AbstractDocumentBuilder docBuilder) {
        super(docBuilder);
    }

    /**
     * <pre>
     * 构建指定目录下的所有文本文件的倒排索引.
     *      需要遍历和解析目录下的每个文本文件, 得到对应的Document对象，再依次加入到索引，并将索引保存到文件.
     * @param rootDirectory ：指定目录
     * @return ：构建好的索引
     * </pre>
     */
    @Override
    public AbstractIndex buildIndex(String rootDirectory) {
        // 调用FileUtil类中的list方法，得到指定目录下的所有文件的绝对路径表list
        List<String> list = FileUtil.list(rootDirectory);
        // 调用Collections类中的sort方法，对list进行默认排序
        Collections.sort(list);
        AbstractIndex index = new Index();
        AbstractDocumentBuilder builder = new DocumentBuilder();
        for(int i=0;i<list.size();i++){
            // 遍历list中的所有文本文件的绝对路径，
            // 对于每一个路径调用builder的build方法，构建Document对象，
            // 然后调用index的addDocument方法，将构建出来的Document对象加入index中。
            index.addDocument(builder.build(docId++, list.get(i), new File(list.get(i))));
        }
        index.optimize();
        return index;
    }
}
