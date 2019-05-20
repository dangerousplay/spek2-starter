package io.sicredi.test

import arrow.core.Try
import arrow.core.getOrDefault
import arrow.core.getOrElse
import arrow.core.orNull
import com.winterbe.expekt.should
import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.examples.helloworld.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature


class AccessMapping : Spek({

    Feature("Cumprimentando Servidor"){
        val channel = ManagedChannelBuilder.forAddress("0.0.0.0", 9090).usePlaintext().build()
        val server = ServerBuilder.forPort(9090).addService(MockGreeter).build().start()

        val client = GreeterGrpc.newBlockingStub(channel)

        Scenario("Envio de nome completo"){
            lateinit var nome: String
            lateinit var response: Try<HelloReply>

            Given("Um nome completo"){
                MockGreeter.sayHelloResponseQueue.clear()

                MockGreeter.sayHelloResponseQueue.apply {
                    addMessage {
                        message = "Hello Davi Ficanha Henrique!"
                    }
                }

                nome = "Davi Ficanha Henrique"
            }

            When("Requesita o servidor com o nome"){
                response = Try {
                    client.sayHello { name = nome }
                }
            }

            Then("Deve conter o nome informado"){
                response.isFailure().should.`false`
                response.orNull()?.message.should.contain(nome).and.startWith("Hello")
            }
        }

        Scenario("Envio de nome vazio"){

            lateinit var nome: String
            lateinit var response: Try<HelloReply>

            Given("Um nome vazio"){
                MockGreeter.sayHelloResponseQueue.clear()

                MockGreeter.sayHelloResponseQueue.apply {
                    addError(Status.Code.INVALID_ARGUMENT.toStatus())
                }

                nome = ""
            }

            When("Requesita o servidor com o nome"){
                response = Try {
                    client.sayHello { name = nome }
                }
            }

            Then("Deve conter uma mensagem de erro que o nome não pode ser vazio"){

                response.isFailure().should.`true`
                response
                    .failed().orNull().should.`is`.instanceof(StatusRuntimeException::class.java)
            }
        }
    }
})