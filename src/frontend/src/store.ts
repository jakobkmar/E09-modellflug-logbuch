import { ref } from 'vue'

export class Protocol {
  readonly uniqueId: number = Math.floor(Math.random() * 10_000)
  readonly user: string = "Max Mustermann"
  constructor(
    readonly date: string,
    readonly time: string,
    readonly signatureDataUrl: string,
  ) {}

  getGermanDateString() {
    const split = this.date.split('-')
    return `${split[2]}.${split[1]}.${split[0]}`
  }
}

export const protocols = ref<Protocol[]>([])
