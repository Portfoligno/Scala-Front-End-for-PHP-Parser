FROM composer:1.7.2 as builder

RUN composer require nikic/php-parser:4.1.0


FROM php:7.2.10-fpm-alpine

COPY --from=builder /app/vendor vendor
