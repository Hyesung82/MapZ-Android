package com.cheocharm.remote.mapper

import com.cheocharm.domain.model.MapZSign
import com.cheocharm.domain.model.MapZSignInRequest
import com.cheocharm.domain.model.MapZSignUpRequest
import com.cheocharm.remote.model.MapZSignResponse
import com.cheocharm.remote.model.request.MapZSignInDto
import com.cheocharm.remote.model.request.MapZSignUpDto

// domain -> remote
internal fun MapZSignUpRequest.toDto(): MapZSignUpDto {
    return MapZSignUpDto(email, password, username)
}

// remote -> domain
internal fun MapZSignResponse.toDomain(): MapZSign {
    return MapZSign(this.accessToken, this.refreshToken)
}

// domain -> remote
internal fun MapZSignInRequest.toDto(): MapZSignInDto {
    return MapZSignInDto(this.email, this.pwd)
}
