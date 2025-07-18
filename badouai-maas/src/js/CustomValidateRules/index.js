import { Validator } from 'vee-validate'
import Date from './Rules/date'

export default {
    install () {
        for (let key in Date) {
            Validator.extend(key, Date[key])
        }
    }
}
