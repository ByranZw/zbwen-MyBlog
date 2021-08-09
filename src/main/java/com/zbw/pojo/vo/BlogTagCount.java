package com.zbw.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BlogTagCount implements Comparable<BlogTagCount>{
    private Integer tagId;

    private String tagName;

    private long tagCount;

    @Override
    public int compareTo(BlogTagCount o) {
        if(this.getTagCount() > o.getTagCount()){
            return -1;
        }else{
            return 1;
        }
    }
}
