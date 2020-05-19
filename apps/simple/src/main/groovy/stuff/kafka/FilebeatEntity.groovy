package stuff.kafka

class FilebeatEntity {
    static class Fields  {
        String master
        String protocol
        String domainSuffix
    }

    static class Log {
        static class File {
            String path
        }

        long offset
        File file
    }

    Date timestamp
    String message
    Fields fields
    Log log
}
