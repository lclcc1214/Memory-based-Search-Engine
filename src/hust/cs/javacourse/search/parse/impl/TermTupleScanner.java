package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

public class TermTupleScanner extends AbstractTermTupleScanner {

    List<String> list = null;
    int nextPos = 0;

    public TermTupleScanner(){}
    public TermTupleScanner(BufferedReader input){
        super(input);
        String s = null;
        try{
            StringBuffer buf = new StringBuffer();
            // 循环读取input中的内容，每次读取一行，防止缓存区爆满
            while((s = input.readLine()) != null){
                buf.append(s).append("\n");
            }
            s = buf.toString().trim();
        } catch (IOException e){
            e.printStackTrace();
        }

        //使用分词器对读取的字符串，根据预设的正则表达式进行分词
        StringSplitter splitter = new StringSplitter();
        splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
        list = splitter.splitByRegex(s);
    }

    /**
     * 获得下一个三元组
     *
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        if(list.isEmpty() || nextPos >= list.size()) {
            // list为空或者已经读到流的末尾
            return null;
        }
        AbstractTermTuple termTuple = new TermTuple();
        // 以list取出的下一个对象，调用toLowerCase方法后得到的对象为参数，创建新的单词term，
        // 并将其作为TermTuple的单词对象。
        // 同时将取出元素位置nextPos作为当前位置curPos。
        termTuple.term = new Term(list.get(nextPos).toLowerCase(Locale.ROOT));
        termTuple.curPos = nextPos;
        nextPos++;
        return termTuple;
    }
}
