"use client";

import { useState, useRef, KeyboardEvent } from "react";

interface ChatInputProps {
  onSend: (message: string) => void;
  disabled?: boolean;
}

export default function ChatInput({ onSend, disabled }: ChatInputProps) {
  const [input, setInput] = useState("");
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const handleSend = () => {
    const trimmed = input.trim();
    if (!trimmed || disabled) return;
    onSend(trimmed);
    setInput("");
    if (textareaRef.current) {
      textareaRef.current.style.height = "auto";
    }
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const handleInput = () => {
    const el = textareaRef.current;
    if (el) {
      el.style.height = "auto";
      el.style.height = Math.min(el.scrollHeight, 160) + "px";
    }
  };

  return (
    <div className="border-t border-gray-800 bg-gray-950 p-4">
      <div className="max-w-3xl mx-auto">
        <div className="flex items-end gap-3 bg-gray-800 rounded-2xl px-4 py-3 border border-gray-700 focus-within:border-blue-500/50 transition-colors">
          <textarea
            ref={textareaRef}
            value={input}
            onChange={(e) => {
              setInput(e.target.value);
              handleInput();
            }}
            onKeyDown={handleKeyDown}
            placeholder="Describe your ideal job..."
            rows={1}
            disabled={disabled}
            className="flex-1 bg-transparent text-sm text-gray-100 placeholder-gray-500 resize-none outline-none max-h-40"
          />
          <button
            onClick={handleSend}
            disabled={disabled || !input.trim()}
            className="shrink-0 w-8 h-8 rounded-lg bg-blue-600 text-white flex items-center justify-center hover:bg-blue-500 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M6 12L3.269 3.126A59.768 59.768 0 0121.485 12 59.77 59.77 0 013.27 20.876L5.999 12zm0 0h7.5" />
            </svg>
          </button>
        </div>
        <p className="text-[10px] text-gray-600 text-center mt-2">
          Job Agent searches Adzuna, Jooble, Remotive &amp; Gmail
        </p>
      </div>
    </div>
  );
}
