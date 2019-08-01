package com.novoda.movies.mvi.search.domain

import com.novoda.movies.mvi.search.BaseStore
import com.novoda.movies.mvi.search.Endpoints
import com.novoda.movies.mvi.search.NetworkDependencyProvider
import com.novoda.movies.mvi.search.ProductionSchedulingStrategy
import com.novoda.movies.mvi.search.data.ApiSearchResultsConverter
import com.novoda.movies.mvi.search.data.MovieDataSource
import com.novoda.movies.mvi.search.data.SearchApi
import com.novoda.movies.mvi.search.data.SearchBackend
import com.novoda.movies.mvi.search.presentation.SearchResultsConverter
import com.novoda.movies.mvi.search.presentation.SearchStore
import com.novoda.movies.mvi.search.presentation.ViewSearchResults

internal class SearchDependencyProvider(
        private val networkDependencyProvider: NetworkDependencyProvider,
        private val endpoints: Endpoints
) {

    private fun provideMovieDataSource(): MovieDataSource {
        val searchApi = networkDependencyProvider.provideRetrofit().create(SearchApi::class.java)
        return SearchBackend(
                searchApi,
                ApiSearchResultsConverter(endpoints)
        )
    }

    fun provideSearchStore(): SearchStore {
        return BaseStore(
            reducer = SearchReducer(provideSearchResultsConverter()),
            schedulingStrategy = ProductionSchedulingStrategy(),
            middlewares = listOf(SearchMiddleware(provideMovieDataSource(), ProductionSchedulingStrategy().work)),
            initialValue = SearchState.Content(queryString = "", results = ViewSearchResults())
        )
    }

    private fun provideSearchResultsConverter() = SearchResultsConverter()
}
