package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.StopWords;

import java.util.Arrays;

public class StopWordTermTupleFilter extends AbstractTermTupleFilter {

    public StopWordTermTupleFilter(AbstractTermTupleStream input){
        super(input);
    }

    /**
     * 获得下一个三元组
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        AbstractTermTuple termTuple = null;
        while((termTuple = input.next()) != null){
            boolean flag = false;
            for(String str: StopWords.STOP_WORDS){
                if(str.equals(termTuple.term.getContent())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                return termTuple;
            }
        }
        return null;
    }
}
