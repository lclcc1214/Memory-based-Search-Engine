package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;

import java.io.*;
import java.util.*;

/**
 * AbstractIndex的具体实现类
 */
public class Index extends AbstractIndex {

    public Index(){}

    /**
     * 返回索引的字符串表示
     *
     * @return 索引的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Index{\n").append("  docId to docPath\n");
        for(Map.Entry<Integer, String> entry: docIdToDocPathMapping.entrySet()){
            builder.append("   #").append(entry.getKey()).append(" ----> ").append(entry.getValue()).append("\n");
        }
        builder.append("  term to postingList\n");
        for(Map.Entry<AbstractTerm, AbstractPostingList> entry: termToPostingListMapping.entrySet()){
            builder.append("    ").append(entry.getKey()).append(" ----> ").append(entry.getValue()).append("\n");
        }
        builder.append("}");
        return new String(builder);
    }

    /**
     * 添加文档到索引，更新索引内部的HashMap
     *
     * @param document ：文档的AbstractDocument子类型表示
     */
    @Override
    public void addDocument(AbstractDocument document) {
        docIdToDocPathMapping.put(document.getDocId(),document.getDocPath());
        Map<AbstractTerm, List<Integer>> termToPosition = new TreeMap<>();//用于暂存倒排索引
        List<AbstractTermTuple> tuples = document.getTuples();
        for(AbstractTermTuple tuple: tuples){
            // 根据三元组对应的单词term是否在termToPosition中存在，
            // 若存在，则仅在termToPosition的单词term对应的PostingList中将当前三元组的curPos加入；
            // 若不存在，则创建一个新的ArrayList，再将curPos加入。
            termToPosition.computeIfAbsent(tuple.term, k -> new ArrayList<>());
            termToPosition.get(tuple.term).add(tuple.curPos);
        }
        for(Map.Entry<AbstractTerm, List<Integer>> entry: termToPosition.entrySet()){
            // 根据entry的key值，即单词term，是否在termToPositionListMapping中存在，
            // 若存在，则在termToPositionListMapping的单词term对应的PostingList中添加新的Posting，参数为文档的编号docId，entry的value的size以及entry的value值；
            // 若不存在，则创建新的PostingList，再进行上述操作。
            termToPostingListMapping.computeIfAbsent(entry.getKey(), k-> new PostingList());
            termToPostingListMapping.get(entry.getKey()).add(new Posting(document.getDocId(), entry.getValue().size(), entry.getValue()));
        }
    }

    /**
     * <pre>
     * 从索引文件里加载已经构建好的索引.内部调用FileSerializable接口方法readObject即可
     * @param file ：索引文件
     * </pre>
     */
    @Override
    public void load(File file) {
        try{
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            readObject(inputStream);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * 将在内存里构建好的索引写入到文件. 内部调用FileSerializable接口方法writeObject即可
     * @param file ：写入的目标索引文件
     * </pre>
     */
    @Override
    public void save(File file) {
        try{
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            writeObject(outputStream);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 返回指定单词的PostingList
     *
     * @param term : 指定的单词
     * @return ：指定单词的PostingList;如果索引字典没有该单词，则返回null
     */
    @Override
    public AbstractPostingList search(AbstractTerm term) {
        return this.termToPostingListMapping.get(term);
    }

    /**
     * 返回索引的字典.字典为索引里所有单词的并集
     *
     * @return ：索引中Term列表
     */
    @Override
    public Set<AbstractTerm> getDictionary() {
        Set<AbstractTerm> set = new TreeSet<>();
        for(Map.Entry<AbstractTerm, AbstractPostingList> entry: termToPostingListMapping.entrySet()){
            set.add(entry.getKey());
        }
        return set;
    }

    /**
     * <pre>
     * 对索引进行优化，包括：
     *      对索引里每个单词的PostingList按docId从小到大排序
     *      同时对每个Posting里的positions从小到大排序
     * 在内存中把索引构建完后执行该方法
     * </pre>
     */
    @Override
    public void optimize() {
        for(Map.Entry<AbstractTerm, AbstractPostingList> entry: termToPostingListMapping.entrySet()){
            AbstractPostingList postingList = entry.getValue();
            postingList.sort();
            for(int i=0;i<postingList.size();i++){
                AbstractPosting posting = postingList.get(i);
                posting.sort();
            }
        }
    }

    /**
     * 根据docId获得对应文档的完全路径名
     *
     * @param docId ：文档id
     * @return : 对应文档的完全路径名
     */
    @Override
    public String getDocName(int docId) {
        return docIdToDocPathMapping.get(docId);
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try{
            out.writeObject(docIdToDocPathMapping);
            out.writeObject(termToPostingListMapping);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     *
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in) {
        try{
            this.docIdToDocPathMapping = (Map<Integer, String>)(in.readObject());
            this.termToPostingListMapping = (Map<AbstractTerm, AbstractPostingList>)(in.readObject());
        } catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
