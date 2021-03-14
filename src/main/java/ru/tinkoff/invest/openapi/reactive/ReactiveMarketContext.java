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
import ru.tinkoff.invest.openapi.MarketContext;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.rest.*;

import java.time.OffsetDateTime;

public class ReactiveMarketContext {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveMarketContext.class);

    private Mono<MarketContext> context;

    public ReactiveMarketContext(OpenApi openApi) {
        this.context = new ReactorOpenApiContextProducer<MarketContext>(2, () -> {
            logger.debug("Creating MarketContext");
            return openApi.getMarketContext();
        }).get();
    }

    /**
     * Асинхронное получение списка акций, доступных для торговли.
     *
     * @return Список акций.
     */
    @NotNull
    public Flux<MarketInstrument> getMarketStocks() {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.getMarketStocks()))
                .flatMapIterable(list -> list.getInstruments());
    }


    /**
     * Асинхронное получение бондов, доступных для торговли.
     *
     * @return Список облигаций.
     */
    @NotNull
    public Flux<MarketInstrument> getMarketBonds() {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.getMarketBonds()))
                .flatMapIterable(list -> list.getInstruments());
    }

    /**
     * Асинхронное получение списка фондов, доступных для торговли.
     *
     * @return Список фондов.
     */
    @NotNull
    public Flux<MarketInstrument> getMarketEtfs() {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.getMarketEtfs()))
                .flatMapIterable(list -> list.getInstruments());
    }

    /**
     * Асинхронное получение списка валют, доступных для торговли.
     *
     * @return Список валют.
     */
    @NotNull
    public Flux<MarketInstrument> getMarketCurrencies() {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.getMarketCurrencies()))
                .flatMapIterable(list -> list.getInstruments());
    }

    /**
     * Асинхронное получение текущего состояния торгового "стакана".
     *
     * @param figi     Идентификатор инструмента.
     * @param depth    Глубина стакана.
     *
     * @return "Стакан" по инструменту или ничего, если инструмент не найден.
     */
    @NotNull
    public Mono<Orderbook> getMarketOrderbook(@NotNull String figi, int depth) {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.getMarketOrderbook(figi, depth)))
                .flatMap(val -> Mono.justOrEmpty(val));
    }

    /**
     * Асинхронное получение исторических данных по свечам.
     *
     * @param figi     Идентификатор инструмента.
     * @param from     Начальный момент рассматриваемого отрезка временного интервала.
     * @param to       Конечный момент рассматриваемого отрезка временного интервала.
     * @param interval Разрешающий интервал свечей.
     *
     * @return Данные по свечам инструмента или ничего, если инструмент не найден.
     */
    @NotNull
    public Flux<Candle> getMarketCandles(@NotNull String figi,
                                           @NotNull OffsetDateTime from,
                                           @NotNull OffsetDateTime to,
                                           @NotNull CandleResolution interval) {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.getMarketCandles(figi, from, to, interval)))
                .flatMap(val -> Mono.justOrEmpty(val)).flatMapIterable(c -> c.getCandles());
    }

    /**
     * Асинхронный поиск инструментов по тикеру.
     *
     * @param ticker Искомый тикер.
     *
     * @return Список инструментов.
     */
    @NotNull
    public Flux<MarketInstrument> searchMarketInstrumentsByTicker(@NotNull String ticker) {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.searchMarketInstrumentsByTicker(ticker)))
                .flatMapIterable(list -> list.getInstruments());
    }

    /**
     * Асинронный поиск инструмента по идентификатору.
     *
     * @param figi Искомый тикер.
     *
     * @return Найденный инструмент или ничего, если инструмент не найден.
     */
    @NotNull
    public Mono<SearchMarketInstrument> searchMarketInstrumentByFigi(@NotNull String figi) {
        return context.flatMap(ctx -> Mono.fromFuture(ctx.searchMarketInstrumentByFigi(figi)))
                .flatMap(val -> Mono.justOrEmpty(val));
    }

}
