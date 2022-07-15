# NATS-lab

for lab

## CLI

https://docs.nats.io/using-nats/nats-tools/nats_cli

啟動一個 CLI

``` bash
docker run --network nats --rm -it synadia/nats-box
```

建立連線

``` bash
nats context save local --server nats://nats:4222 --description 'Local Host' --select
```

列出 Stream

``` bash
nats str ls
```

新增 Stream

``` bash
nats str add ORDERS_STORE \
    --subjects "ORDERS.*" \
    --ack \
    --max-msgs=-1 \
    --max-bytes=-1 \
    --max-age=1y \
    --storage file \
    --retention limits \
    --max-msg-size=-1 \
    --discard old \
    --dupe-window="0s" \
    --replicas 1
```





## Lab01

Core NATS - Publish Subscribe model  
![Publish Subscribe model ](https://683899388-files.gitbook.io/~/files/v0/b/gitbook-x-prod.appspot.com/o/spaces%2F-LqMYcZML1bsXrN3Ezg0%2Fuploads%2Fgit-blob-22d59af386038cc2717176561ffc95c63c295926%2Fpubsub.svg?alt=media)

## Lab02

Core NATS - Queue Subscriptions model  
![Queue Subscriptions model](https://683899388-files.gitbook.io/~/files/v0/b/gitbook-x-prod.appspot.com/o/spaces%2F-LqMYcZML1bsXrN3Ezg0%2Fuploads%2Fgit-blob-e4f2a6428a4be494475b4c811af461ff0908ec2a%2Fqueues.svg?alt=media)

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