# NATS-lab

for lab
https://docs.nats.io/nats-concepts/jetstream/consumers/example_configuration

## CLI

https://docs.nats.io/using-nats/nats-tools/nats_cli

啟動一個 CLI

``` bash
docker run --network nats --rm -it synadia/nats-box
```

建立連線

``` bash
nats context save local --server nats://nats1:4222 --description 'Local Host' --select
```

列出 Stream

``` bash
nats str ls
```

新增 Stream

``` bash
nats str add ORDERS_STR \
  --subjects "ORDERS.*" \
  --storage file \
  --retention limits \
  --discard old \
  --max-msgs=-1 \
  --max-bytes=-1 \
  --max-age=1y \
  --max-msg-size=-1 \
  --dupe-window="0s" \
  --replicas 1 \
  --ack
```
ORDERS_STORE: Stream 名稱  
subjects: 訂閱主題  
storage: 儲存類型 file/memory  
retention: 持久化方式 limits/interest/workq
  limits: 可限制儲存的數量大小等
  interest: 已經有消費者才會儲存
  workq: 駐列模式
discard: 消息滿了之後如何處理
  old: 將舊消息刪除
  new: 不接收新消息
max-msgs: 所有消息儲存的最大數量
max-bytes: 所有消息儲存的大小
max-age: 消息儲存的最長時間, 超過會自動刪除
max-msg-size: 單一消息的大小
dupe-window: 根據 Msg-Id 的 header 判斷唯一訊息的時間
replicas: 消息複本數量

ack
no-ack

```
Stream ORDERS_STR was created

Information for Stream ORDERS_STR created 2022-07-19T07:31:51Z

Configuration:

             Subjects: ORDERS.*
     Acknowledgements: true
            Retention: File - Limits
             Replicas: 1
       Discard Policy: Old
     Duplicate Window: 2m0s
     Maximum Messages: unlimited
        Maximum Bytes: unlimited
          Maximum Age: 1y0d0h0m0s
 Maximum Message Size: unlimited
    Maximum Consumers: unlimited


Cluster Information:

                 Name: C1
               Leader: n1-c1

State:

             Messages: 0
                Bytes: 0 B
             FirstSeq: 0
              LastSeq: 0
     Active Consumers: 0
```

allow-rollup: 是否可以取代前面消息
deny-delete: 是否可以刪除消息
deny-purge: 是否可以清空 stream

https://docs.nats.io/using-nats/developer/develop_jetstream/model_deep_dive



## 消費者

``` bash
nats consumer add ORDERS_STR NEW \
  --filter ORDERS.received \
  --pull \
  --ack explicit \
  --deliver all \
  --replay=instant \
  --max-deliver=-1 \
  --max-pending=0 \
  --sample 100

nats consumer add ORDERS_STR DISPATCH \
  --filter ORDERS.processed \
  --pull \
  --ack explicit \
  --deliver all \
  --replay=instant \
  --max-deliver=-1 \
  --max-pending=0 \
  --sample 100

nats consumer add ORDERS_STR MONITOR \
  --filter '' \
  --target monitor.ORDERS \
  --ack none \
  --deliver last \
  --replay instant \
  --heartbeat=0s \
  --no-flow-control
```

Stream 名稱
Consumer 名稱
filter: 過濾 Stream 中的訊息
ack: 是否需要做消息確認
  none: 不需要
  all: 一次全部確認
  explicit: 必須確認每個單獨的消息
deliver: 消費者的起始位置策略
  All: 預設值, 從頭開始的所有訊息
  Last: 從最後一個開始
  New: 只處理建立後的新訊息
  ByStartSequence: 從指定的序號開始處理
  ByStartTime: 從指定的時間開始處理
  LastPerSubject: 從每個主題的會後一個消息開始
replay: 訊息回放的機制
  original: 依照原本的
  instant: 立即消費所有訊息
max-deliver: 最大投遞次數
max-pending: 最多允許多少消息未投遞成功, 到這數量就不再投遞
sample: 監控抽樣
target: 目的地 (推送模式)

https://nats.io/blog/jetstream-java-client-03-consume/

## Lab01

Core NATS - Publish Subscribe model  
![Publish Subscribe model ](https://683899388-files.gitbook.io/~/files/v0/b/gitbook-x-prod.appspot.com/o/spaces%2F-LqMYcZML1bsXrN3Ezg0%2Fuploads%2Fgit-blob-22d59af386038cc2717176561ffc95c63c295926%2Fpubsub.svg?alt=media)

## Lab02

Core NATS - Queue Subscriptions model  
![Queue Subscriptions model](https://683899388-files.gitbook.io/~/files/v0/b/gitbook-x-prod.appspot.com/o/spaces%2F-LqMYcZML1bsXrN3Ezg0%2Fuploads%2Fgit-blob-e4f2a6428a4be494475b4c811af461ff0908ec2a%2Fqueues.svg?alt=media)

## Lab 8

NATS + Cloudevent

## Reference
[https://nats.io/blog/jetstream-java-client-01-stream-create/](https://nats.io/blog/jetstream-java-client-01-stream-create/)  
[https://docs.nats.io/using-nats/developer/develop_jetstream](https://docs.nats.io/using-nats/developer/develop_jetstream)  
[https://nats.io/blog/sync-async-publish-java-client/#synchronous-and-asynchronous-publishing-with-the-nats-java-library](https://nats.io/blog/sync-async-publish-java-client/#synchronous-and-asynchronous-publishing-with-the-nats-java-library)
[https://github.com/nats-io/java-nats-examples/blob/main/hello-world/src/main/java/io/nats/hello/PublishAsync.java](https://github.com/nats-io/java-nats-examples/blob/main/hello-world/src/main/java/io/nats/hello/PublishAsync.java)  
[https://stackabuse.com/asynchronous-pubsub-messaging-in-java-with-nats-jetstream/](https://stackabuse.com/asynchronous-pubsub-messaging-in-java-with-nats-jetstream/)
[https://ithelp.ithome.com.tw/articles/10247408](https://ithelp.ithome.com.tw/articles/10247408)   
[https://engineering.linecorp.com/zh-hant/blog/20210226-golang-event-driven/](https://engineering.linecorp.com/zh-hant/blog/20210226-golang-event-driven/)  
[https://juejin.cn/post/7088572152303058975](https://juejin.cn/post/7088572152303058975)  
[https://www.byronruth.com/grokking-nats-consumers-part-1/](https://www.byronruth.com/grokking-nats-consumers-part-1/)  
[https://nats.io/blog/jetstream-java-client-05-pull-subscribe/](https://nats.io/blog/jetstream-java-client-05-pull-subscribe/)  
[https://youtu.be/LZIsYKyv1WA](https://youtu.be/LZIsYKyv1WA)  
[https://www.gushiciku.cn/pl/aEw8/zh-tw](https://www.gushiciku.cn/pl/aEw8/zh-tw)  
[https://juejin.cn/post/6952792674655010853](https://juejin.cn/post/6952792674655010853)  
  
[https://github.com/nats-io/spring-nats](https://github.com/nats-io/spring-nats)  

[https://www.freesion.com/article/9781848819/](https://www.freesion.com/article/9781848819/)  
[https://github.com/nats-io/spring-nats/blob/main/nats-spring-cloud-stream-binder/src/main/java/io/nats/cloud/stream/binder/NatsMessageProducer.java](https://github.com/nats-io/spring-nats/blob/main/nats-spring-cloud-stream-binder/src/main/java/io/nats/cloud/stream/binder/NatsMessageProducer.java)  
[https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream.html#_sending_arbitrary_data_to_an_output_e_g_foreign_event_driven_sources](https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream.html#_sending_arbitrary_data_to_an_output_e_g_foreign_event_driven_sources)  
[https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream.html#_functional_composition](https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream.html#_functional_composition)  

[https://cloudevents.github.io/sdk-java/spring#messaging](https://cloudevents.github.io/sdk-java/spring#messaging)  
不會用 CloudEvents 的 id  
[https://github.com/spring-cloud/spring-cloud-function/blob/main/spring-cloud-function-context/src/main/java/org/springframework/cloud/function/cloudevent/CloudEventMessageBuilder.java#L197](https://github.com/spring-cloud/spring-cloud-function/blob/main/spring-cloud-function-context/src/main/java/org/springframework/cloud/function/cloudevent/CloudEventMessageBuilder.java#L197)  
[https://youtu.be/61aOCovpz5Y](https://youtu.be/61aOCovpz5Y)  
[https://youtu.be/wOhihZHex8g](https://youtu.be/wOhihZHex8g)  
[https://youtu.be/NGuiizWUuaw](https://youtu.be/NGuiizWUuaw)
[https://docs.ksyun.com/documents/41718](https://docs.ksyun.com/documents/41718)  
[https://bootify.io/mongodb/offset-date-time-with-spring-boot-mongodb.html](https://bootify.io/mongodb/offset-date-time-with-spring-boot-mongodb.html)

## mongo

``` js
db.orders.find({})
   .projection({})
   .sort({time:-1})
   .limit(100)
```

``` js
db.orders.find({ orderID: "12"})
   .projection({})
   .sort({_id:-1})
   .limit(100)
```
