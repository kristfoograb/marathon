package com.malinskiy.marathon.cli.config.deserialize

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.malinskiy.marathon.execution.strategy.RetryStrategy
import com.malinskiy.marathon.execution.strategy.SortingStrategy
import com.malinskiy.marathon.execution.strategy.impl.retry.NoRetryStrategy
import com.malinskiy.marathon.execution.strategy.impl.retry.fixedquota.FixedQuotaRetryStrategy
import com.malinskiy.marathon.execution.strategy.impl.sorting.ExecutionTimeSortingStrategy
import com.malinskiy.marathon.execution.strategy.impl.sorting.NoSortingStrategy
import com.malinskiy.marathon.execution.strategy.impl.sorting.SuccessRateSortingStrategy

class RetryStrategyDeserializer : StdDeserializer<RetryStrategy>(RetryStrategy::class.java) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): RetryStrategy {
        val codec = p?.codec as ObjectMapper
        val node: JsonNode = codec.readTree(p) ?: throw RuntimeException("Missing retry strategy")
        val type = node.get("type").asText()

        return when (type) {
            "no-retry" -> NoRetryStrategy()
            "fixed-quota" -> {
                (node as ObjectNode).remove("type")
                return codec.treeToValue<FixedQuotaRetryStrategy>(node)
            }
            else -> throw RuntimeException("Unrecognized retry strategy $type")
        }
    }
}