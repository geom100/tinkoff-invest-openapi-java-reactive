/*
Copyright 2021 Mikhail Rumyantsev <michael.rumyantsev@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package ru.tinkoff.invest.openapi.reactive;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.tinkoff.invest.openapi.Context;

import java.time.Duration;
import java.util.function.Supplier;

public class ReactorOpenApiContextProducer <T extends Context> {

    private static final Logger logger = LoggerFactory.getLogger(ReactorOpenApiContextProducer.class);

    private RateLimiter limiter;
    private Supplier<T> contextSupplier;

    public ReactorOpenApiContextProducer(double rateLimit, Supplier<T> contextSupplier) {
        this.limiter = RateLimiter.create(rateLimit);
        this.contextSupplier = contextSupplier;
    }

    public Mono<T> get() {
        return Mono.defer(() -> {
            if (limiter.tryAcquire()) {
                logger.debug("Acquired successfully. Thread {}", Thread.currentThread().getName());
                return Mono.just(contextSupplier.get());
            } else {
                logger.debug("Not acquired. Thread {}", Thread.currentThread().getName());
                return Mono.empty();
            }
        })
         .retryWhen(Retry.fixedDelay(Long.MAX_VALUE, Duration.ofMillis(1000)));
    }

}
