/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.fn.filter;

import java.util.function.Function;

import reactor.core.publisher.Flux;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.instrument.messaging.MessagingSleuthOperators;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.transformer.ExpressionEvaluatingTransformer;
import org.springframework.messaging.Message;

/**
 * @author Artem Bilan
 * @author David Turanski
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FilterFunctionProperties.class)
public class FilterFunctionConfiguration {

	//@Bean
	//public Function<Flux<Message<?>>, Flux<Message<?>>> filterFunction(
	//		ExpressionEvaluatingTransformer filterExpressionEvaluatingTransformer) {
	//
	//	return flux ->
	//			flux.filter((message) ->
	//					(Boolean) filterExpressionEvaluatingTransformer.transform(message).getPayload());
	//}

	@Bean
	public Function<Flux<Message<?>>, Flux<Message<?>>> filterFunction(
			ExpressionEvaluatingTransformer filterExpressionEvaluatingTransformer, BeanFactory beanFactory) {
		return flux ->
				// message headers
				// b3 = 11111111-22222222-1
				// b3 = traceid-spanid-sampled
				flux.map(message -> MessagingSleuthOperators.forInputMessage(beanFactory, message))
						// ^^^ message contains header
						// key = traceHandlerParentSpan, value - parentSpan -> spanId = 22222222
						// key = Span.class, value - child span -> spanId = 3333333
						.filter((message) -> (Boolean) filterExpressionEvaluatingTransformer.transform(message).getPayload())
						.map(message -> MessagingSleuthOperators.handleOutputMessage(beanFactory, message));
						// ^^^ we instrument the outbound message with tracing headers
						// message headers
						// b3 = 11111111-33333333-1
						// b3 = traceid-spanid-sampled
	}

	@Bean
	public ExpressionEvaluatingTransformer filterExpressionEvaluatingTransformer(
			FilterFunctionProperties filterFunctionProperties) {

		return new ExpressionEvaluatingTransformer(filterFunctionProperties.getExpression());
	}

}
