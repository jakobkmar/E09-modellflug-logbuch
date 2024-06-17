import { ref, computed } from 'vue';

export class Protocol {
  readonly uniqueId: number = Math.floor(Math.random() * 10_000);
  readonly user: string = "Max Mustermann";
  active = true;
  endTime: string | null = null; // TODO use date type
  notes: string | null = null;

  constructor(
    readonly date: string,
    readonly startTime: string,
    readonly signatureDataUrl: string,
  ) {}

  getGermanDateString() {
    const split = this.date.split('-');
    return `${split[2]}.${split[1]}.${split[0]}`;
  }
}

export const protocols = ref<Protocol[]>([]);

export const activeProtocols = computed(() => {
  return protocols.value.filter((p) => p.active);
});
export const finishedProtocols = computed(() => {
  return protocols.value.filter((p) => !p.active);
});

export class Pilot {
  constructor(
    public id: number,
    public lastName: string,
    public firstName: string,
    public username: string,
    public password: string,
    public flightLeaderPermission: boolean,
    public flightLeaderPermissionFile: string | ArrayBuffer | null,
    public membershipType: 'Pilot' | 'Tagespilot' | 'Gast'
  ) {}
}

export const pilots = ref<Pilot[]>([
  // Example pilot
  {
    id: 1,
    lastName: 'Doe',
    firstName: 'John',
    username: 'john.doe',
    password: 'password123',
    flightLeaderPermission: true,
    flightLeaderPermissionFile: null,
