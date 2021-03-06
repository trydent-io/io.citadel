module io.citadel {
  requires io.vertx.core;
  requires io.vertx.client.sql;
  requires io.vertx.client.sql.templates;
  requires io.vertx.client.jdbc;
  requires io.vertx.client.sql.pg;
  requires io.vertx.config;
  requires org.flywaydb.core;
  requires com.fasterxml.jackson.datatype.jsr310;
  requires com.fasterxml.jackson.databind;
  requires org.slf4j;
  requires org.apache.logging.log4j;
  requires java.sql;
  requires com.zaxxer.hikari;
  requires org.postgresql.jdbc;
}
