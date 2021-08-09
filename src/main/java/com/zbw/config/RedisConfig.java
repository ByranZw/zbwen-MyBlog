package com.zbw.config;

/**
 * 配置redis的存取
 *
 * @blame mqpearth
 */
public class RedisConfig {


    /**
     * redis中存放 首页博客 数量 的最大值
     */
    public static final int REDIS_INDEX_BLOG_COUNT = 10;

    /**
     * 缓存博客总数
     */
    public static final String REDIS_BLOG_TOTAL = "TOTAL_COUNT";

    public static final String REDIS_BLOG_TOTAL_PAGE = "TOTAL_PAGE";

    /**
     * redis中存放 首页博客 ID 的 key
     */
    public static final String REDIS_INDEX_BLOG = "INDEX_BLOG";

    /**
     * redis中存放blog的前缀,拼接上id后获取
     */
    public static final String REDIS_BLOG_PREFIX = "BLOG_";

    /**
     * 存放友链的key
     */
    public static final String REDIS_LINK = "LINK";

    public static final String REDIS_LINK_PREFIX = "LINK_";



    /**
     * IP_127.0.0.1
     */
    public static final String REDIS_IP_PREFIX = "IP_";


    /**
     * 请求频率限制 缓存时间
     */
    public static final long REDIS_LIMIT_REQUEST_FREQUENCY_TIME = 100L;


    /**
     * 缓存   系统设置  的key 拼接上id后获取
     */
    public static final String REDIS_CONFIG_PREFIX = "CONFIG_";

    /**
     * 缓存  系统设置 ID  的KEY
     */
    public static final String REDIS_CONFIG = "CONFIG";

    /**
     * redis中存放 分类目录 的key
     */
    public static final String REDIS_CATEGORY = "CATEGORY";

    public static final String REDIS_CATEGORY_PREFIX = "CATEGORY_";







}