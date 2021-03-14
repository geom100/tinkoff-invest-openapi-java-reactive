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
import ru.tinkoff.invest.openapi.PortfolioContext;
import ru.tinkoff.invest.openapi.model.rest.CurrencyPosition;
import ru.tinkoff.invest.openapi.model.rest.PortfolioPosition;

public class ReactivePortfolioContext {

    private static final Logger logger = LoggerFactory.getLogger(ReactivePortfolioContext.class);

    private Mono<PortfolioContext> context;

    public ReactivePortfolioContext(OpenApi openApi) {
        this.context = new ReactorOpenApiContextProducer<PortfolioContext>(2, () -> {
            logger.debug("Creating PortfolioContext");
            return openApi.getPortfolioContext();
        }).get();
    }

    /**
     * Асинхронное получение информации по портфелю инструментов.
     *
     * @param brokerAccountId Идентификатор брокерского счёта.
     *
     * @return Портфель инструментов.
     */
    @NotNull
    public Flux<PortfolioPosition> getPortfolio(@Nullable String brokerAccountId) {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.getPortfolio(brokerAccountId)))
                .flatMapIterable(p -> p.getPositions());
    }

    /**
     * Асинхронное получение информации по валютным активам.
     *
     * @param brokerAccountId Идентификатор брокерского счёта.
     *
     * @return Портфель валют.
     */
    @NotNull
    public Flux<CurrencyPosition> getPortfolioCurrencies(@Nullable String brokerAccountId) {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.getPortfolioCurrencies(brokerAccountId)))
                .flatMapIterable(c -> c.getCurrencies());
    }
}
