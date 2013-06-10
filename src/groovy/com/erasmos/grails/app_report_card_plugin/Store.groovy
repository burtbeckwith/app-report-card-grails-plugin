package com.erasmos.grails.app_report_card_plugin


enum Store {

    UnitedArabEmirates('AE','United Arab Emirates'),
    AntiguaAndBarbuda('AG','Antigua & Barbuda'),
    Anguilla('AI','Anguilla'),
    Armenia('AM','Armenia'),
    Angola('AO','Angola'),
    Argentina('AR','Argentina'),
    Austria('AT','Austria'),
    Australia('AU','Australia'),
    Azerbaijan('AZ','Azerbaijan'),
    Belgium('BE','Belgium'),
    Bulgaria('BG','Bulgaria'),
    Bahrain('BH','Bahrain'),
    Bermuda('BM','Bermuda'),
    BruneiDarussalam('BN','Brunei Darussalam'),
    Bolivia('BO','Bolivia'),
    Brazil('BR','Brazil'),
    Bahamas('BS','Bahamas'),
    Botswana('BW','Botswana'),
    Belarus('BY','Belarus'),
    Belize('BZ','Belize'),
    Canada('CA','Canada'),
    Switzerland('CH','Switzerland'),
    Chile('CL','Chile'),
    China('CN','China'),
    Colombia('CO','Colombia'),
    CostaRica('CR','Costa Rica'),
    Cyprus('CY','Cyprus'),
    CzechRepublic('CZ','Czech Republic'),
    Germany('DE','Germany'),
    Denmark('DK','Denmark'),
    Dominica('DM','Dominica'),
    DominicanRepublic('DO','Dominican Republic'),
    Algeria('DZ','Algeria'),
    Ecuador('EC','Ecuador'),
    Estonia('EE','Estonia'),
    Egypt('EG','Egypt'),
    Spain('ES','Spain'),
    Finland('FI','Finland'),
    France('FR','France'),
    UnitedKingdom('GB','United Kingdom'),
    Grenada('GD','Grenada'),
    Ghana('GH','Ghana'),
    Greece('GR','Greece'),
    Guatemala('GT','Guatemala'),
    Guyana('GY','Guyana'),
    HongKong('HK','Hong Kong'),
    Honduras('HN','Honduras'),
    Croatia('HR','Croatia'),
    Hungary('HU','Hungary'),
    Indonesia('ID','Indonesia'),
    Ireland('IE','Ireland'),
    Israel('IL','Israel'),
    India('IN','India'),
    Iceland('IS','Iceland'),
    Italy('IT','Italy'),
    Jamaica('JM','Jamaica'),
    Jordan('JO','Jordan'),
    Japan('JP','Japan'),
    Kenya('KE','Kenya'),
    StKittsAndNevis('KN','St. Kitts & Nevis'),
    KoreaRepublicOf('KR','Korea, Republic Of'),
    Kuwait('KW','Kuwait'),
    CaymanIslands('KY','Cayman Islands'),
    Kazakhstan('KZ','Kazakhstan'),
    Lebanon('LB','Lebanon'),
    StLucia('LC','St. Lucia'),
    SriLanka('LK','Sri Lanka'),
    Lithuania('LT','Lithuania'),
    Luxembourg('LU','Luxembourg'),
    Latvia('LV','Latvia'),
    MoldovaRepublicOf('MD','Moldova, Republic Of'),
    Madagascar('MG','Madagascar'),
    MacedoniaTheFormerYugoslavRepublicOf('MK','Macedonia, The Former Yugoslav Republic Of'),
    Mali('ML','Mali'),
    Macao('MO','Macao'),
    Montserrat('MS','Montserrat'),
    Malta('MT','Malta'),
    Mauritius('MU','Mauritius'),
    Mexico('MX','Mexico'),
    Malaysia('MY','Malaysia'),
    Niger('NE','Niger'),
    Nigeria('NG','Nigeria'),
    Nicaragua('NI','Nicaragua'),
    Netherlands('NL','Netherlands'),
    Norway('NO','Norway'),
    NewZealand('NZ','New Zealand'),
    Oman('OM','Oman'),
    Panama('PA','Panama'),
    Peru('PE','Peru'),
    Philippines('PH','Philippines'),
    Pakistan('PK','Pakistan'),
    Poland('PL','Poland'),
    Portugal('PT','Portugal'),
    Paraguay('PY','Paraguay'),
    Qatar('QA','Qatar'),
    Romania('RO','Romania'),
    Russia('RU','Russia'),
    SaudiArabia('SA','Saudi Arabia'),
    Sweden('SE','Sweden'),
    Singapore('SG','Singapore'),
    Slovenia('SI','Slovenia'),
    Slovakia('SK','Slovakia'),
    Senegal('SN','Senegal'),
    Suriname('SR','Suriname'),
    ElSalvador('SV','El Salvador'),
    TurksAndCaicos('TC','Turks & Caicos'),
    Thailand('TH','Thailand'),
    Tunisia('TN','Tunisia'),
    Turkey('TR','Turkey'),
    TrinidadAndTobago('TT','Trinidad & Tobago'),
    Taiwan('TW','Taiwan'),
    Tanzania('TZ','Tanzania'),
    Uganda('UG','Uganda'),
    UnitedStates('US','United States'),
    Uruguay('UY','Uruguay'),
    Uzbekistan('UZ','Uzbekistan'),
    StVincentAndTheGrenadines('VC','St. Vincent & The Grenadines'),
    Venezuela('VE','Venezuela'),
    BritishVirginIslands('VG','British Virgin Islands'),
    Vietnam('VN','Vietnam'),
    Yemen('YE','Yemen'),
    SouthAfrica('ZA','South Africa');

    String code // http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
    String name

    Store(final String code, final String name){
        this.code   = code
        this.name   = name
    }

    public static Store findByCode(final String code){
        return Store.values().find{it.code==code}
    }

    public static List<Store> getAllSortedByName(){
        return values().sort {a,b->a.name <=> b.name}
    }
}
