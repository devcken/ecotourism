final SPRING_PROFILES_ACTIVE = System.getProperty('spring.profiles.active') == null ?
        System.getenv('SPRING_PROFILES_ACTIVE') : System.getProperty('spring.profiles.active')
final ROLLING_FILE_APPENDER = 'ROLLING_FILE'
final CONSOLE_APPENDER = 'CONSOLE'

if (SPRING_PROFILES_ACTIVE == 'dev' || SPRING_PROFILES_ACTIVE == 'prod') {
    appender(ROLLING_FILE_APPENDER, RollingFileAppender) {
        file = '/var/log/kakaopay/ecotourism/application.log'
        append = true
        rollingPolicy(TimeBasedRollingPolicy) {
            fileNamePattern = '/var/log/kakaopay/ecotourism/application.%d{yyyy-MM-dd}.log'
            maxHistory = 3
        }
        encoder(PatternLayoutEncoder) {
            pattern = '[%d{yyyy-MM-dd HH:mm:ss.SSS}] %level @ %logger{20} > %m%n'
        }
    }

    root(ERROR, [ROLLING_FILE_APPENDER])
} else {
    appender(CONSOLE_APPENDER, ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
            pattern = '[%d{HH:mm:ss.SSS}] %t %-5level @ %logger{20} > %m%n'
        }
    }

    root(ERROR, [CONSOLE_APPENDER])

    logger('com.kakaopay', DEBUG)
    logger('org.springframework.web', DEBUG)
    logger('org.springframework.jdbc.core.JdbcTemplate', DEBUG)
    logger('org.springframework.jdbc.core.StatementCreatorUtils', DEBUG)
    logger('org.hibernate.SQL', DEBUG)
    logger('org.hibernate.type.descriptor.sql.BasicBinder', DEBUG)
}
