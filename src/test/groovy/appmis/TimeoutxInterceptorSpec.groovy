package appmis

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class TimeoutxInterceptorSpec extends Specification implements InterceptorUnitTest<TimeoutxInterceptor> {

    def setup() {
    }

    def cleanup() {

    }

    void "Test timeoutx interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"timeoutx")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
