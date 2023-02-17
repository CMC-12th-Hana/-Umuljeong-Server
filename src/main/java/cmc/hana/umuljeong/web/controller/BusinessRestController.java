package cmc.hana.umuljeong.web.controller;

import cmc.hana.umuljeong.auth.annotation.AuthUser;
import cmc.hana.umuljeong.converter.BusinessConverter;
import cmc.hana.umuljeong.domain.Business;
import cmc.hana.umuljeong.domain.ClientCompany;
import cmc.hana.umuljeong.domain.Member;
import cmc.hana.umuljeong.domain.mapping.BusinessMember;
import cmc.hana.umuljeong.service.BusinessMemberService;
import cmc.hana.umuljeong.service.BusinessService;
import cmc.hana.umuljeong.service.ClientCompanyService;
import cmc.hana.umuljeong.service.MemberService;
import cmc.hana.umuljeong.web.dto.BusinessRequestDto;
import cmc.hana.umuljeong.web.dto.BusinessResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BusinessRestController {

    private final BusinessService businessService;
    private final ClientCompanyService clientCompanyService;
    private final BusinessMemberService businessMemberService;


    @GetMapping("/company/client/business/{businessId}")
    public ResponseEntity<BusinessResponseDto.BusinessDto> getBusiness(@PathVariable(name = "businessId") Long businessId, @AuthUser Member member) {
        // todo : 해당 사업이 멤버의 회사에 속한 사업인지 검증
        Business business = businessService.findById(businessId);
        return ResponseEntity.ok(BusinessConverter.toBusinessDto(business));
    }

    @GetMapping("/company/client/{clientId}/businesses")
    public ResponseEntity<BusinessResponseDto.BusinessListDto> getBusinessList(@PathVariable(name = "clientId") Long clientCompanyId, @AuthUser Member member) {
        // todo : 존재하는 id 인지 & 멤버의 회사에 속한 고객사인지 검증
        ClientCompany clientCompany = clientCompanyService.findById(clientCompanyId);
        List<Business> businessList = businessService.findByClientCompany(clientCompany);
        return ResponseEntity.ok(BusinessConverter.toBusinessListDto(businessList));
    }

    @PostMapping("/company/client/business")
    public ResponseEntity<BusinessResponseDto.CreateBusinessDto> createBusiness(@RequestBody BusinessRequestDto.CreateBusinessDto request) {
        Business business = businessService.create(request);
        return ResponseEntity.ok(BusinessConverter.toCreateBusinessDto(business));
    }
}
