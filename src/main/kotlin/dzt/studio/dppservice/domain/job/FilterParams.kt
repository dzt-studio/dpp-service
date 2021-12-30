package dzt.studio.dppservice.domain.job

import dzt.studio.dppservice.util.PageRequest

class FilterParams: PageRequest() {
    var jobName:String? = null
    var jobStatus:String? = null
}