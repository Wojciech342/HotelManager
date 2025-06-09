import {
  Component,
  Input,
  Output,
  EventEmitter,
  forwardRef,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-star-rating',
  templateUrl: './star-rating.component.html',
  styleUrls: ['./star-rating.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => StarRatingComponent),
      multi: true,
    },
  ],
})
export class StarRatingComponent implements ControlValueAccessor {
  @Input() readonly: boolean = false;
  @Output() ratingChange = new EventEmitter<number>();

  stars: number[] = [1, 2, 3, 4, 5];
  rating: number = 0;
  hoverRating: number | null = null;

  private onChange = (rating: number) => {};
  private onTouched = () => {};

  setRating(value: number) {
    if (!this.readonly) {
      this.rating = value;
      this.onChange(this.rating);
      this.ratingChange.emit(this.rating);
      this.onTouched();
    }
  }

  writeValue(value: number): void {
    this.rating = value || 0;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setHover(value: number | null) {
    if (!this.readonly) {
      this.hoverRating = value;
    }
  }

  getMouseValue(event: MouseEvent, star: number): number {
    const { left, width } = (
      event.target as HTMLElement
    ).getBoundingClientRect();
    const x = event.clientX - left;
    return x < width / 2 ? star - 0.5 : star;
  }
}
