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
import reactor.core.publisher.Mono;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.OrdersContext;
import ru.tinkoff.invest.openapi.model.rest.LimitOrderRequest;
import ru.tinkoff.invest.openapi.model.rest.MarketOrderRequest;
import ru.tinkoff.invest.openapi.model.rest.PlacedLimitOrder;
import ru.tinkoff.invest.openapi.model.rest.PlacedMarketOrder;

public class ReactiveOrdersContext {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveOrdersContext.class);

    private Mono<OrdersContext> context;

    public ReactiveOrdersContext(OpenApi openApi) {
        this.context = new ReactorOpenApiContextProducer<OrdersContext>(0.83, () -> {
            logger.debug("Creating OrdersContext");
            return openApi.getOrdersContext();
        }).get();
    }

    /**
     * Размещение лимитной заявки.
     *
     * @param figi Идентификатор инструмента.
     * @param limitOrder Параметры отправляемой заявки.
     * @param brokerAccountId Идентификатор брокерского счёта.
     *
     * @return Размещённая заявка.
     */
    @NotNull
    public Mono<PlacedLimitOrder> placeLimitOrder(@NotNull String figi,
                                                    @NotNull LimitOrderRequest limitOrder,
                                                    @Nullable String brokerAccountId) {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.placeLimitOrder(figi, limitOrder, brokerAccountId)));
    }

    /**
     * Размещение рыночной заявки.
     *
     * @param figi Идентификатор инструмента.
     * @param marketOrder Параметры отправляемой заявки.
     * @param brokerAccountId Идентификатор брокерского счёта.
     *
     * @return Размещённая заявка.
     */
    @NotNull
    public Mono<PlacedMarketOrder> placeMarketOrder(@NotNull String figi,
                                                      @NotNull MarketOrderRequest marketOrder,
                                                      @Nullable String brokerAccountId) {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.placeMarketOrder(figi, marketOrder, brokerAccountId)));
    }

    /**
     * Отзыв лимитной заявки.
     *
     * @param orderId Идентификатор заявки.
     * @param brokerAccountId Идентификатор брокерского счёта.
     *
     * @return Ничего.
     */
    @NotNull
    public Mono<Void> cancelOrder(@NotNull String orderId, @Nullable String brokerAccountId) {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.cancelOrder(orderId, brokerAccountId)));
    }
}
