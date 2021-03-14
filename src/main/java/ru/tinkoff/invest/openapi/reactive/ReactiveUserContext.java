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

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.UserContext;
import ru.tinkoff.invest.openapi.model.rest.UserAccount;

public class ReactiveUserContext {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveUserContext.class);

    private Mono<UserContext> context;

    public ReactiveUserContext(OpenApi openApi) {
        this.context = new ReactorOpenApiContextProducer<UserContext>(2, () -> {
            logger.debug("Creating UserContext");
            return openApi.getUserContext();
        }).get();
    }

    /**
     * Асинхронное получение списка брокерских счетов.
     *
     * @return Список счетов.
     */
    @NotNull
    public Flux<UserAccount> getAccounts() {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.getAccounts()))
                .flatMapIterable(a -> a.getAccounts());
    }
}
