# 公共配置文件
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>
spring:
  application:
    name: ${docsConfig.description}
  # 默认 dev 环境
  profiles:
    active: dev
  # 支持 swagger3
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
  # session 配置
  session:
    # todo 取消注释开启分布式 session（须先配置 Redis）
    store-type: redis
    # 30 天过期
    timeout: 2592000
  # 数据库配置
  # todo 需替换配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: ${mysqlConfig.url}
    username: ${mysqlConfig.username}
    password: ${mysqlConfig.username}
  # Redis 配置
  # todo 需替换配置，然后取消注释
<#if needRedis>
  redis:
    database: 1
    host: localhost
    port: 6379
    timeout: 5000
</#if>
<#if needEs>
  # Elasticsearch 配置
  # todo 需替换配置，然后取消注释
  elasticsearch:
    uris: http://localhost:9200
    username: elastic
    password: uZQdpcuZYg2jFgwix3hJ
</#if>
  # 文件上传
  servlet:
    multipart:
      # 大小限制
      max-file-size: 10MB
server:
  address: 0.0.0.0
  port: 8101
  servlet:
    context-path: /api
    # cookie 30 天过期
    session:
      cookie:
        max-age: 2592000
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: isDelete # 全局逻辑删除的实体字段名
      logic-delete-value: 1 # 逻辑已删除值（默认为 1）
      logic-not-delete-value: 0 # 逻辑未删除值（默认为 0）
# 微信相关
wx:
  # 微信公众平台
  # todo 需替换配置
  mp:
    token: xxx
    aesKey: xxx
    appId: xxx
    secret: xxx
    config-storage:
      http-client-type: HttpClient
      key-prefix: wx
      redis:
        host: 127.0.0.1
        port: 6379
      type: Memory
  # 微信开放平台
  # todo 需替换配置
  open:
    appId: xxx
    appSecret: xxx
# 对象存储
# todo 需替换配置
cos:
  client:
    accessKey: xxx
    secretKey: xxx
    region: xxx
    bucket: xxx
# ${docsConfig.title}配置
knife4j:
  enable: true
  openapi:
    title: "${docsConfig.title}"
    version: ${docsConfig.version}
    group:
      default:
        api-rule: package
        api-rule-resources:
          - ${basePackage}.springbootinit.controller
