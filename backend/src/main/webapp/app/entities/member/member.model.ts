export interface IMember {
  id: number;
  name?: string | null;
  email?: string | null;
  phone?: string | null;
  active?: boolean | null;
}

export type NewMember = Omit<IMember, 'id'> & { id: null };
