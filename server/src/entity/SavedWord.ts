import {
  Column,
  CreateDateColumn,
  Entity,
  Generated,
  JoinColumn,
  ManyToOne,
  OneToMany,
  PrimaryColumn,
  PrimaryGeneratedColumn,
  UpdateDateColumn,
} from "typeorm";
import { Definition } from "./Definition";
import { Pronunciation } from "./Pronunciation";
import { User } from "./User";

@Entity()
export class SavedWord {
  @PrimaryGeneratedColumn("uuid")
  id: string;

  @Column()
  value: string;

  @Column()
  language: string;

  @Column()
  topics: string;

  @Column("uuid")
  userId: string;
  @ManyToOne(() => User, (user) => user.savedWords, {
    onDelete: "CASCADE",
    onUpdate: "CASCADE",
  })
  @JoinColumn({ name: "userId" })
  user: User;

  @Column()
  position: number;

  @OneToMany(() => Definition, (definition) => definition.word)
  definitions: Definition[];

  @OneToMany(() => Pronunciation, (pronunciation) => pronunciation.word)
  pronunciations: Pronunciation[];

  @CreateDateColumn()
  createdAt: Date;

  @UpdateDateColumn()
  updatedAt: Date;
}
