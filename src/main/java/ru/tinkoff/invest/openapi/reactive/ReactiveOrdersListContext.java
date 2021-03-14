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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.OrdersContext;
import ru.tinkoff.invest.openapi.model.rest.Order;

public class ReactiveOrdersListContext {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveOrdersListContext.class);

    private Mono<OrdersContext> context;

    public ReactiveOrdersListContext(OpenApi openApi) {
        this.context = new ReactorOpenApiContextProducer<OrdersContext>(1.65, () -> {
            logger.debug("Creating OrdersContext");
            return openApi.getOrdersContext();
        }).get();
    }

    /**
     * Асинхронное получение списка активных заявок.
     *
     * @param brokerAccountId Идентификатор брокерского счёта.
     *
     * @return Список заявок.
     */
    @NotNull
    public Flux<Order> getOrders(@Nullable String brokerAccountId) {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.getOrders(brokerAccountId)))
                .flatMapIterable(list -> list);
    }
}
