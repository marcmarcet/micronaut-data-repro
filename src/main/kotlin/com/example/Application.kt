package com.example

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.Micronaut.build
import io.micronaut.scheduling.TaskExecutors.IO
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Singleton
import javax.transaction.Transactional

fun main(args: Array<String>) {
    build()
        .args(*args)
        .packages("com.example")
        .start()
}

@Controller
open class CreateCompanyController(
    private val companyService: CompanyService,
    private val companyRepository: CompanyRepository,
) {
    @Post("/companies")
    open suspend fun create(): Long {
        try {
            companyService.create()
        } catch (_: Exception) {
            // all good, changes should be gone now.
        }

        // if @Transactional works, should always return 0.
        return companyRepository.count()

    }
}

@Singleton
open class CompanyService(
    private val companyRepository: CompanyRepository,
) {
    @Transactional
    open suspend fun create(): Company {
        val company =
            companyRepository.save(
                Company(
                    "Test Co"
                )
            )

        throw Exception("Error!! This should rollback all the changes.")
    }
}

@JdbcRepository(dialect = Dialect.H2)
interface CompanyRepository : GenericRepository<Company, Long> {
    suspend fun save(company: Company): Company
    suspend fun count(): Long
}


@MappedEntity
class Company(@field:Id val name: String)