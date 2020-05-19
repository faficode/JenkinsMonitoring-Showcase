package stuff.kafka

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import spock.lang.Specification

import java.time.Instant

class KafkaSpike extends Specification {
    def 'send something'() {
        when:
        def client = new ProducerClient<>('localhost:9092', 'test-client', Serdes.String().serializer())

        client.init()
        10.times {
            client.send('testtopic', 'm1', 'hi there ' + it)
        }

        then:
        sleep(5000)
    }

    def 'send filebeat data'() {
        when:
        def client = new ProducerClient<>('kafka-0.kafka:9092',
                'filebeat-2', KafkaConsts.JSON_SE)
        client.init()

        def start = Instant.now()
        100.times {
            def log = new FilebeatEntity(
                    timestamp: new Date(),
                    message: 'Started by user [8mha:////blablub[0mtestuser',
                    fields: new FilebeatEntity.Fields(
                            master: 'test-jenkins',
                            protocol: 'https',
                            domainSuffix: 'streams.nowhere'
                    ),
                    log: new FilebeatEntity.Log(offset: 100 * it,
                            file: new FilebeatEntity.Log.File(path: "/var/jenkins/builds/testbuild/${start.toString()}/${it}/log"))
            )

            def log2 = new FilebeatEntity(
                    timestamp: new Date(),
                    message: 'hi there',
                    fields: new FilebeatEntity.Fields(
                            master: 'test-jenkins',
                            protocol: 'https',
                            domainSuffix: 'streams.nowhere'
                    ),
                    log: new FilebeatEntity.Log(offset: 100 * it,
                            file: new FilebeatEntity.Log.File(path: "/var/jenkins/builds/testbuild/${start.toString()}/${it}/log"))
            )

            def random = Random.newInstance()
            def log3 = new FilebeatEntity(
                    timestamp: new Date(),
                    message: 'Finished: ' + ['SUCCESS', 'FAILURE', 'ABORTED', 'UNSTABLE', 'NOT_BUILT'][random.nextInt(5)],
                    fields: new FilebeatEntity.Fields(
                            master: 'test-jenkins',
                            protocol: 'https',
                            domainSuffix: 'streams.nowhere'
                    ),
                    log: new FilebeatEntity.Log(offset: 100 * it,
                            file: new FilebeatEntity.Log.File(path: "/var/jenkins/builds/testbuild/${start.toString()}/${it}/log"))
            )

            client.send('filebeat-1', null, log)
            client.send('filebeat-1', null, log2)
            client.send('filebeat-1', null, log3)

            sleep(50)
        }

        then:
        sleep(1000)
    }

    def 'do some streams stuff'() {
        when:
        println 'test'

        new StreamsRunner('kafka-0.kafka:9092', 'testapp', 'latest') {
            @Override
            StreamsBuilder createTopologyBuilder() {
                def builder = new StreamsBuilder()
                KStream<String, String> stream = builder.stream('testtopic', Consumed.with(Serdes.String(), Serdes.String()))

                stream
                        .peek { key, value -> println value }
                        .to('someothertopic')

                builder
            }
        }.start()

        println 'active'

        sleep(1000000)

        then:
        true
    }

    def 'poll something'() {
        when:
        def poll = new LoopClient<>('localhost:9092', 'poll-client')

        poll.init('testtopic', { records ->
            records.forEach {
                println it.value()
            }
        })

        then:
        sleep(60000)
    }
}
