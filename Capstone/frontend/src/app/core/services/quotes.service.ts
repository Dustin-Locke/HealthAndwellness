import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class QuotesService {
  private quotes: string[] = [
    'Small steps every day = big wins.',
    'Progress, not perfection.',
    'Your body hears everything your mind says. Stay positive!',
    "Take care of your body. It's the only place you have to live.",
    "The only bad workout is the one that didn't happen.",
    "You don't have to be extreme, just consistent.",
    'Believe in yourself and all that you are.',
    'Every journey begins with a single step.',
    'Your health is an investment, not an expense.',
    'Strive for progress, not perfection.',
    'The pain you feel today will be the strength you feel tomorrow.',
    'Success is the sum of small efforts repeated day in and day out.',
    "Don't wish for it, work for it.",
    'Your only limit is you.',
    'Push yourself because no one else is going to do it for you.',
    'Great things never come from comfort zones.',
    'Dream it. Wish it. Do it.',
    "Success doesn't just find you. You have to go out and get it.",
    "The harder you work for something, the greater you'll feel when you achieve it.",
    "Don't stop when you're tired. Stop when you're done.",
    'Wake up with determination. Go to bed with satisfaction.',
    'Do something today that your future self will thank you for.',
    'Little things make big days.',
    "It's going to be hard, but hard does not mean impossible.",
    "Don't wait for opportunity. Create it.",
    'Sometimes later becomes never. Do it now.',
    'The key to success is to focus on goals, not obstacles.',
    'Dream bigger. Do bigger.',
    "Fitness is not about being better than someone else. It's about being better than you used to be.",
    "Your body can stand almost anything. It's your mind that you have to convince.",
    'A one hour workout is only 4% of your day. No excuses.',
    'Sore today, strong tomorrow.',
    'Sweat is fat crying.',
    "The best project you'll ever work on is you.",
    'You are one workout away from a good mood.',
    'Making excuses burns zero calories per hour.',
    "If it doesn't challenge you, it won't change you.",
    'Discipline is choosing between what you want now and what you want most.',
    "You didn't come this far to only come this far.",
    'The difference between try and triumph is a little umph.',
    'Eat clean, train dirty.',
    'Your health account is your bank account.',
    "Fitness is like a relationship. You can't cheat and expect it to work.",
    'Be stronger than your excuses.',
    'The body achieves what the mind believes.',
    'Motivation is what gets you started. Habit is what keeps you going.',
    'Fall in love with taking care of yourself.',
    'Every rep counts. Every step matters.',
    'You are worth the effort.',
    'Transformation happens one choice at a time.',
  ];

  // Get a random quote from the collection
  getRandomQuote(): string {
    const randomIndex = Math.floor(Math.random() * this.quotes.length);
    return this.quotes[randomIndex];
  }

  // Get all quotes (useful for testing or displaying multiple)
  getAllQuotes(): string[] {
    return [...this.quotes];
  }

  // Get a specific number of random quotes (no duplicates)
  getRandomQuotes(count: number): string[] {
    const shuffled = [...this.quotes].sort(() => 0.5 - Math.random());
    return shuffled.slice(0, Math.min(count, this.quotes.length));
  }
}
