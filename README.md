# ego(易购网)

项目所用技术：Spring mvc , Mybatis , Spring Security, Zookeeper , redis ,RabbitMQ , Solr

### ego_api:接口模块
  定义通用接口。
  
### ego_commons:公用模块
  存放工具类，页面所需的一些通用模板
  
### ego_item: 商品条目模块
  实现对商品条目的缓存，查询。第一次访问时，进行Redis缓存
  
### ego_manager: 后端模块
  实现对商品，广告等之类的CRUD，同时发送消息实现Resdis 缓存，通过Spring Security 实现授权
  
### ego_mapper: 数据库模块
  数据库操作模块
  
### ego_passport: 前端登录注册模块
  实现前台页面的登录
  
### ego_pojo: pojo类
  与数据库对应的pojo类
  
### ego_protal: 前端主页面模块
  显示前台主页面。第一次访问时，进行Redis缓存
  
### ego_rabbitmq_receiver： 消息接收模块
  接收消息并处理消息
  
### ego_rabbitmq_send: 消息发送模块
  消息发送方的通用模块
  
### ego_redis: 缓存模块
  缓存通用模块
### ego_search: 商品搜索模块
  通过Solr技术实现商品查询，并缓存
