export interface ICoach {
  id: number;
  name?: string | null;
  specialty?: string | null;
}

export type NewCoach = Omit<ICoach, 'id'> & { id: null };
